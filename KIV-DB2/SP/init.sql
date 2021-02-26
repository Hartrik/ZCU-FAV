
-- SEKVENCE

DROP SEQUENCE LAST_ID;
CREATE SEQUENCE LAST_ID
    START WITH 0
    INCREMENT BY 1
    MINVALUE 0
    MAXVALUE 999999999;

-- TABULKY

DELETE FROM MINA;
DELETE FROM TAH;
DELETE FROM HRA;
DELETE FROM POLE;
DELETE FROM OBLAST;
DELETE FROM OBTIZNOST;
DELETE FROM STAV;
DELETE FROM OMEZENI;

DROP TABLE MINA;
DROP TABLE TAH;
DROP TABLE HRA;
DROP TABLE POLE;
DROP TABLE OBLAST;
DROP TABLE OBTIZNOST;
DROP TABLE STAV;
DROP TABLE OMEZENI;

-- tabulka s vyctem stavu hry
CREATE TABLE STAV (
    ID INTEGER NOT NULL,
    NAZEV VARCHAR2(20 CHAR) NOT NULL,
    
    PRIMARY KEY(ID)
);
INSERT INTO STAV VALUES (1, 'rozehraná');
INSERT INTO STAV VALUES (2, 'úspìšnì ukonèená');
INSERT INTO STAV VALUES (3, 'neúspìšnì ukonèená');

-- tabulka s obtiznostmi
CREATE TABLE OBTIZNOST (
    ID INTEGER NOT NULL, 
	RADKY INTEGER NOT NULL, 
	SLOUPCE INTEGER NOT NULL, 
	MINY INTEGER NOT NULL, 
	NAZEV VARCHAR2(10 CHAR) NOT NULL,
    
    PRIMARY KEY(ID)
);
INSERT INTO OBTIZNOST VALUES (1, 9, 9, 10, 'zaèáteèník');
INSERT INTO OBTIZNOST VALUES (2, 16, 16, 40, 'pokroèilý');
INSERT INTO OBTIZNOST VALUES (3, 16, 30, 99, 'expert');

-- tabulka s omezenimy
CREATE TABLE OMEZENI (
    MAX_POLI INTEGER NOT NULL, 
    MIN_POLI INTEGER NOT NULL, 
    MAX_POKRYTI_PROCENT INTEGER NOT NULL
);
INSERT INTO OMEZENI VALUES (100, 9, 40);

-- herni plocha
CREATE TABLE OBLAST (
    ID INTEGER NOT NULL,
    OBTIZNOST_ID INTEGER, -- null pokud je vlastni
    
    -- kopiruje se z OBTIZNOST nebo vlastni
    RADKY INTEGER NOT NULL,
    SLOUPCE INTEGER NOT NULL,
    MINY INTEGER NOT NULL,
    
    PRIMARY KEY(ID),
    FOREIGN KEY(OBTIZNOST_ID) REFERENCES OBTIZNOST(ID)
);

-- policko herni plochy
CREATE TABLE POLE (
    ID INTEGER NOT NULL,
    X INTEGER NOT NULL,
    Y INTEGER NOT NULL,
    MINA INTEGER NOT NULL,
    ODKRYTO INTEGER DEFAULT 0,
    OBLAST_ID INTEGER NOT NULL,
    
    PRIMARY KEY(ID),
    FOREIGN KEY(OBLAST_ID) REFERENCES OBLAST(ID)
);

-- tabulka hracem oznacenych min
CREATE TABLE MINA (
    POLE_ID INTEGER NOT NULL,
    
    PRIMARY KEY(POLE_ID),
    FOREIGN KEY(POLE_ID) REFERENCES POLE(ID)
);

CREATE TABLE TAH (
    ID INTEGER NOT NULL,
    CASOVA_ZNACKA TIMESTAMP(6) NOT NULL,
    POLE_ID INTEGER NOT NULL,

    PRIMARY KEY(ID),
    FOREIGN KEY(POLE_ID) REFERENCES POLE(ID)
);

CREATE TABLE HRA (
    ID INTEGER NOT NULL,
    PRVNI_TAH TIMESTAMP(6),
    POSLEDNI_TAH TIMESTAMP(6),
    OZNACENO_MIN INTEGER DEFAULT 0,
    STAV_ID INTEGER DEFAULT 1,
    OBLAST_ID INTEGER NOT NULL,  -- navic
    
    PRIMARY KEY(ID),
    FOREIGN KEY(STAV_ID) REFERENCES STAV(ID),
    FOREIGN KEY(OBLAST_ID) REFERENCES OBLAST(ID)
);

-- PROCEDURY

-- nahodne vygeneruje miny
CREATE OR REPLACE PROCEDURE ZAMINUJ_OBLAST(mId IN INTEGER, mRadky IN INTEGER, mSloupce IN INTEGER, mMiny IN INTEGER) AS
    mX INTEGER;
    mY INTEGER;
    mPolozenoMin INTEGER := 0;
    mPoleRow POLE%ROWTYPE;
BEGIN
    WHILE mPolozenoMin < mMiny LOOP
        mX := dbms_random.value(1, mRadky);
        mY := dbms_random.value(1, mSloupce);

        SELECT * INTO mPoleRow
            FROM POLE
            WHERE POLE.OBLAST_ID = mId AND POLE.X = mX AND POLE.Y = mY;

        IF mPoleRow.MINA = 0 THEN
            UPDATE POLE SET POLE.MINA = -1 WHERE POLE.ID = mPoleRow.ID;
            mPolozenoMin := mPolozenoMin + 1;
        END IF;
    END LOOP;
END;
/

-- pro každé nezaminované pole v oblasti spoèítá, s kolika zaminovanými poli sousedí
CREATE OR REPLACE PROCEDURE SPOCITEJ_OBLAST(mOblastId IN INTEGER) AS
    mPocetMin INTEGER;
    CURSOR mKurzor IS SELECT * FROM POLE WHERE OBLAST_ID = mOblastId AND MINA = 0;
BEGIN
    FOR mPole IN mKurzor LOOP
        mPocetMin := 0;
        SELECT COUNT(*) INTO mPocetMin FROM POLE WHERE
                (OBLAST_ID = mOblastId AND MINA = -1)
            AND (X BETWEEN mPole.X - 1 AND mPole.X + 1)
            AND (Y BETWEEN mPole.Y - 1 AND mPole.Y + 1);

        UPDATE POLE SET MINA = mPocetMin WHERE ID = mPole.ID;
    END LOOP;
END;
/

CREATE OR REPLACE PROCEDURE OZNAC_MINY(mOblastId in INTEGER) AS
    -- poles s minou, které ještì nejsou v tabulce mina
    CURSOR mKurzor IS SELECT * FROM POLE p WHERE p.OBLAST_ID = mOblastId AND p.MINA = -1 
        AND NOT EXISTS (SELECT ID FROM MINA m WHERE m.POLE_ID = p.ID);
BEGIN
    FOR mPole IN mKurzor LOOP
        INSERT INTO MINA (POLE_ID) VALUES (mPole.ID);
    END LOOP;
END;
/

CREATE OR REPLACE PROCEDURE ODKRYJ_POLE(mPole in POLE%ROWTYPE) AS
    CURSOR mPoleOkolo(mcOblastID IN INTEGER, mcPoleId IN INTEGER, mcX IN INTEGER, mcY IN INTEGER) IS
        SELECT * FROM POLE p WHERE
                    p.OBLAST_ID = mcOblastID
                AND p.ID != mcPoleId
                AND p.Y BETWEEN mcY - 1 AND mcY + 1
                AND p.X BETWEEN mcX - 1 AND mcX + 1;
BEGIN
    UPDATE POLE SET ODKRYTO = 1 WHERE ID = mPole.ID;    

    -- odkrytí sousedních polí
    IF mPole.MINA = 0 THEN
        FOR p IN mPoleOkolo(mPole.OBLAST_ID, mPole.ID, mPole.X, mPole.Y) LOOP
            IF p.ODKRYTO = 0 THEN
                ODKRYJ_POLE(p);
            END IF;
        END LOOP;
    END IF;
END;
/

-- testovací procedura, která odkryje všechna pole, na kterých nejsou miny
CREATE OR REPLACE PROCEDURE ODKRYJ_OBLAST(mOblastId in INTEGER) AS
    CURSOR mKurzor IS SELECT * FROM POLE WHERE OBLAST_ID = mOblastId AND MINA != -1;
BEGIN
    FOR mDalsiPole IN mKurzor LOOP
        IF mDalsiPole.ODKRYTO = 0 THEN
            dbms_output.put_line('Odkrývání: x=' || mDalsiPole.X || ' y=' || mDalsiPole.Y);
            BEGIN
                INSERT INTO TAH (POLE_ID) VALUES (mDalsiPole.ID);
            EXCEPTION
                WHEN others THEN NULl;
            END;
        END IF;
    END LOOP;
END;
/

-- FUNKCE

CREATE OR REPLACE FUNCTION SPATNY_PARAMETR(mRadky in INTEGER, mSloupce in INTEGER, mMiny IN INTEGER) RETURN BOOLEAN AS
    mOmezeni OMEZENI%ROWTYPE;
BEGIN
    SELECT * INTO mOmezeni FROM OMEZENI WHERE ROWNUM = 1;
    
    IF mRadky NOT BETWEEN mOmezeni.MIN_POLI AND mOmezeni.MAX_POLI THEN
        RETURN TRUE;
    END IF;
    
    IF mSloupce NOT BETWEEN mOmezeni.MIN_POLI AND mOmezeni.MAX_POLI THEN
        RETURN TRUE;
    END IF;
    
    IF (mMiny / (mRadky * mSloupce) * 100) > mOmezeni.MAX_POKRYTI_PROCENT THEN
        RETURN TRUE;
    END IF;
    
    RETURN FALSE;
END;
/

CREATE OR REPLACE FUNCTION MNOHO_MIN(mOblast OBLAST%ROWTYPE, mHra HRA%ROWTYPE) RETURN BOOLEAN AS
BEGIN
    IF mOblast.MINY = mHra.OZNACENO_MIN THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;    
END;
/

CREATE OR REPLACE FUNCTION VYHRA(mOblast OBLAST%ROWTYPE) RETURN BOOLEAN AS
    mNeodkryto INTEGER;
BEGIN
    SELECT COUNT(*) INTO mNeodkryto FROM POLE WHERE OBLAST_ID = mOblast.ID AND odkryto = 0;
    IF mOblast.MINY = mNeodkryto THEN
        dbms_output.put_line('Výhra!');
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;    
END;
/

CREATE OR REPLACE FUNCTION ODKRYTA_MINA(mPole POLE%ROWTYPE) RETURN BOOLEAN AS
BEGIN
    IF mPole.MINA = -1 THEN
        dbms_output.put_line('Šlápl jsi na minu!');
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;    
END;
/

CREATE OR REPLACE FUNCTION RADEK_OBLASTI(mOblastId in INTEGER, mX in INTEGER) RETURN VARCHAR2 AS
    mMinaOznacena INTEGER;
    mRadka varchar2(300 CHAR) := '';
    CURSOR mKurzor IS SELECT * FROM POLE WHERE OBLAST_ID = mOblastId AND X = mX ORDER BY Y;
BEGIN
    FOR mPole IN mKurzor LOOP
        SELECT COUNT(*) INTO mMinaOznacena FROM MINA WHERE POLE_ID = mPole.ID;
        IF mMinaOznacena > 0 THEN
            mRadka := mRadka || ' ! ';
        ELSE
            IF mPole.ODKRYTO > 0 THEN
                IF mPole.MINA != -1 THEN
                    mRadka := mRadka || ' ' || mPole.MINA || ' ';
                ELSE
                    mRadka := mRadka || ' # ';
                END IF;
            ELSE
                mRadka := mRadka || ' . ';
            END IF;
        END IF;
    END LOOP;
    RETURN mRadka;
END;
/

-- TRIGGERY

CREATE OR REPLACE TRIGGER T_OBLAST_BEFORE_INSERT BEFORE INSERT ON OBLAST FOR EACH ROW
DECLARE
    mObtiznost OBTIZNOST%ROWTYPE;
BEGIN
    IF :NEW.OBTIZNOST_ID IS NULL THEN
        -- vlastni obtiznost - nutna kontrola
        IF SPATNY_PARAMETR(:new.RADKY, :new.SLOUPCE, :new.MINY) THEN
            raise_application_error(-20001, 'Vlastní obtížnost nesplòuje omezení');
        END IF;
    ELSE
        -- ziskani informaci o obtiznosti
        SELECT * INTO mObtiznost FROM OBTIZNOST WHERE ID = :NEW.OBTIZNOST_ID;
        :NEW.RADKY := mObtiznost.RADKY;
        :NEW.SLOUPCE := mObtiznost.SLOUPCE;
        :NEW.MINY := mObtiznost.MINY;
    END IF;

    -- vyplneni vynechanych hodnot
    IF :NEW.ID IS NULL THEN
        :NEW.ID := LAST_ID.NEXTVAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER T_OBLAST_AFTER_INSERT AFTER INSERT ON OBLAST FOR EACH ROW
BEGIN
    -- zalozeni hry
    INSERT INTO HRA (ID, OBLAST_ID) VALUES (LAST_ID.NEXTVAL, :new.ID);

    -- vygenerovani poli
    FOR mX IN 1..:NEW.RADKY LOOP
        FOR mY IN 1..:NEW.SLOUPCE LOOP
            INSERT INTO POLE
                (ID, X, Y, MINA, OBLAST_ID)
                VALUES
                (LAST_ID.NEXTVAL, mX, mY, 0, :NEW.ID);
        END LOOP;
    END LOOP;
    
    -- vygenerovani min
    ZAMINUJ_OBLAST(:NEW.ID, :NEW.RADKY, :NEW.SLOUPCE, :NEW.MINY);
    SPOCITEJ_OBLAST(:NEW.ID);
END;
/

CREATE OR REPLACE TRIGGER T_MINA_BEFORE_INSERT BEFORE INSERT ON MINA FOR EACH ROW
DECLARE
    mPole POLE%ROWTYPE;
    mOblast OBLAST%ROWTYPE;
    mHra HRA%ROWTYPE;
BEGIN
    SELECT * INTO mPole FROM POLE WHERE ID = :new.POLE_ID;
    SELECT * INTO mOblast FROM OBLAST WHERE ID = mPole.OBLAST_ID;
    SELECT * INTO mHra FROM HRA WHERE OBLAST_ID = mPole.OBLAST_ID;
    
    -- kontrola poètu oznaèených min
    IF MNOHO_MIN(mOblast, mHra) THEN
        raise_application_error(-20001, 'Již není možné oznaèovat další miny');
    END IF;
    
    -- kontrola stavu hry
    IF mHra.STAV_ID != 1 THEN
        raise_application_error(-20001, 'Již není možné oznaèovat miny');
    END IF;
    
    -- kontrola odkryti
    IF mPole.ODKRYTO != 0 THEN
        raise_application_error(-20001, 'Pole je již odkryté');
    END IF;
    
    -- update poètu oznaèenych min
    UPDATE HRA SET OZNACENO_MIN = (mHra.OZNACENO_MIN + 1) WHERE ID = mHra.ID;
END;
/

CREATE OR REPLACE TRIGGER T_MINA_AFTER_DELETE AFTER DELETE ON MINA FOR EACH ROW
DECLARE
    mPole POLE%ROWTYPE;
    mHra HRA%ROWTYPE;
BEGIN
    SELECT * INTO mPole FROM POLE WHERE ID = :old.POLE_ID;
    SELECT * INTO mHra FROM HRA WHERE OBLAST_ID = mPole.OBLAST_ID;

    -- update poètu oznaèenych min
    UPDATE HRA SET OZNACENO_MIN = (mHra.OZNACENO_MIN - 1) WHERE ID = mHra.ID;
END;
/

CREATE OR REPLACE TRIGGER T_TAH_BEFORE_INSERT BEFORE INSERT ON TAH FOR EACH ROW
DECLARE
    mPole POLE%ROWTYPE;
    mOblast OBLAST%ROWTYPE;
    mHra HRA%ROWTYPE;
    mMinaOznacena INTEGER;
BEGIN
    SELECT * INTO mPole FROM POLE WHERE ID = :new.POLE_ID;
    SELECT * INTO mOblast FROM OBLAST WHERE ID = mPole.OBLAST_ID;
    SELECT * INTO mHra FROM HRA WHERE OBLAST_ID = mPole.OBLAST_ID;

    -- vyplneni vynechanych hodnot
    IF :NEW.ID IS NULL THEN
        :NEW.ID := LAST_ID.NEXTVAL;
    END IF;
    :NEW.CASOVA_ZNACKA := CURRENT_DATE;
    
    -- kontrola stavu hry
    IF mHra.STAV_ID != 1 THEN
        raise_application_error(-20001, 'Již není možné odkrývat pole');
    END IF;
    
    -- kontrola odkryti - nelze odkryt odkryte pole 
    IF mPole.ODKRYTO != 0 THEN
        raise_application_error(-20001, 'Pole je již odkryté; x=' || mPole.X || ' y=' || mPole.Y);
    END IF;
    
    -- kontrola oznaceni - nelze odkryt oznacene pole
    SELECT COUNT(*) INTO mMinaOznacena FROM MINA WHERE POLE_ID = mPole.ID;
    IF mMinaOznacena > 0 THEN
        raise_application_error(-20001, 'Pole je oznaèené, nelze jej odkrýt; x=' || mPole.X || ' y=' || mPole.Y);
    END IF;
    
    -- samotné odkryti
    IF ODKRYTA_MINA(mPole) THEN
        -- neúspìch
        UPDATE HRA SET STAV_ID = 3 WHERE ID = mHra.ID;
        ODKRYJ_POLE(mPole);
    ELSE
        ODKRYJ_POLE(mPole);
        
        -- kontrola vyhry
        IF VYHRA(mOblast) THEN
            -- úspìch
            OZNAC_MINY(mOblast.ID);
            UPDATE HRA SET STAV_ID = 2 WHERE ID = mHra.ID;
        END IF;
    END IF;
    
    -- update èasových znaèek
    IF mHra.PRVNI_TAH IS NULL THEN
        UPDATE HRA SET PRVNI_TAH = :NEW.CASOVA_ZNACKA WHERE ID = mHra.ID;
    END IF;
    UPDATE HRA SET POSLEDNI_TAH = :NEW.CASOVA_ZNACKA WHERE ID = mHra.ID;
END;
/

-- VIEWS

CREATE OR REPLACE VIEW OBLAST_TISK (OBLAST_ID, X, RADKA) AS
    SELECT DISTINCT OBLAST_ID, X, RADEK_OBLASTI(OBLAST_ID, X)
    FROM POLE ORDER BY X;

CREATE OR REPLACE VIEW CHYBNE_MINY AS
    SELECT OBLAST_ID, X, Y
    FROM POLE p INNER JOIN MINA m ON m.POLE_ID = p.ID WHERE p.MINA != -1;
    
CREATE OR REPLACE VIEW VITEZOVE AS
    SELECT h.ID, o.RADKY, o.SLOUPCE, o.MINY, (h.POSLEDNI_TAH - h.PRVNI_TAH) AS TRVANI
    FROM HRA h INNER JOIN OBLAST o ON h.OBLAST_ID = o.ID WHERE h.STAV_ID = 2 ORDER BY (h.POSLEDNI_TAH - h.PRVNI_TAH);

CREATE OR REPLACE VIEW PORAZENI AS
    SELECT h.ID, o.RADKY, o.SLOUPCE, o.MINY,
        (SELECT COUNT(*) FROM MINA m INNER JOIN POLE p ON m.POLE_ID = p.ID WHERE p.OBLAST_ID = h.OBLAST_ID AND p.MINA = -1) AS MINY_SPRAVNE,
        (h.POSLEDNI_TAH - h.PRVNI_TAH) AS TRVANI
    FROM HRA h INNER JOIN OBLAST o ON h.OBLAST_ID = o.ID WHERE h.STAV_ID = 3 ORDER BY (h.POSLEDNI_TAH - h.PRVNI_TAH);

CREATE OR REPLACE TRIGGER T_OBLAST_AFTER_INSERT
    AFTER INSERT ON OBLAST FOR EACH ROW
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
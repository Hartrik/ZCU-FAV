CREATE OR REPLACE TRIGGER T_OBLAST_BEFORE_INSERT
    BEFORE INSERT ON OBLAST FOR EACH ROW
DECLARE
    mObtiznost OBTIZNOST%ROWTYPE;
BEGIN
    IF :NEW.OBTIZNOST_ID IS NULL THEN
        -- vlastni obtiznost - nutna kontrola
        IF SPATNY_PARAMETR(:new.RADKY, :new.SLOUPCE, :new.MINY) THEN
            raise_application_error(
                    -20001, 'Vlastní obtížnost nesplňuje omezení');
        END IF;
    ELSE
        -- ziskani informaci o obtiznosti
        SELECT * INTO mObtiznost FROM OBTIZNOST
                WHERE ID = :NEW.OBTIZNOST_ID;
        :NEW.RADKY := mObtiznost.RADKY;
        :NEW.SLOUPCE := mObtiznost.SLOUPCE;
        :NEW.MINY := mObtiznost.MINY;
    END IF;

    -- vyplneni vynechanych hodnot
    IF :NEW.ID IS NULL THEN
        :NEW.ID := LAST_ID.NEXTVAL;
    END IF;
END;
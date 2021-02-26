CREATE OR REPLACE FUNCTION SPATNY_PARAMETR(mRadky in INTEGER,
        mSloupce in INTEGER, mMiny IN INTEGER) RETURN BOOLEAN AS
    mOmezeni OMEZENI%ROWTYPE;
BEGIN
    SELECT * INTO mOmezeni FROM OMEZENI WHERE ROWNUM = 1;
    
    IF mRadky NOT BETWEEN mOmezeni.MIN_POLI AND mOmezeni.MAX_POLI THEN
        RETURN TRUE;
    END IF;
    
    IF mSloupce NOT BETWEEN mOmezeni.MIN_POLI AND mOmezeni.MAX_POLI THEN
        RETURN TRUE;
    END IF;
    
    IF (mMiny / (mRadky * mSloupce) * 100) >
            mOmezeni.MAX_POKRYTI_PROCENT THEN
        RETURN TRUE;
    END IF;
    
    RETURN FALSE;
END;
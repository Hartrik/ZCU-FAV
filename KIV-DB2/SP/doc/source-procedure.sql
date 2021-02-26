CREATE OR REPLACE PROCEDURE ZAMINUJ_OBLAST(
        mId IN INTEGER, mRadky IN INTEGER, mSloupce IN INTEGER,
        mMiny IN INTEGER) AS
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
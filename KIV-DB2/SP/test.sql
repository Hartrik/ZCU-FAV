----------------------------------------------------------------------------
--- ZAH�JEN� HRY
----------------------------------------------------------------------------
-- pozn. prvn� vytvo�en� oblast bude m�t ID=0

INSERT INTO OBLAST
    (OBTIZNOST_ID)
    VALUES
    ((SELECT ID FROM OBTIZNOST WHERE NAZEV = 'za��te�n�k'));

INSERT INTO OBLAST
    (RADKY, SLOUPCE, MINY)
    VALUES
    (10, 10, 5);

----------------------------------------------------------------------------
--- OZNA�EN� MIN
----------------------------------------------------------------------------

INSERT INTO MINA
    (POLE_ID)
    VALUES
    ((SELECT ID FROM POLE WHERE X = 5 AND Y = 4 AND OBLAST_ID = 0));

DELETE FROM MINA WHERE
    POLE_ID = ((SELECT ID FROM POLE WHERE X = 2 AND Y = 2 AND OBLAST_ID = 0));

-- ozna�en� v�ech min - pro testov�n�
EXECUTE OZNAC_MINY(0);

----------------------------------------------------------------------------
--- ODKRYT� POL��KA
----------------------------------------------------------------------------

INSERT INTO TAH
    (POLE_ID)
    VALUES
    ((SELECT ID FROM POLE WHERE X = 3 AND Y = 5 AND OBLAST_ID = 0));

-- odkryt� v�ech nezaminovan�ch pol� - pro testov�n�
EXECUTE ODKRYJ_OBLAST(0);

----------------------------------------------------------------------------
--- TISK OBLASTI
----------------------------------------------------------------------------

SELECT RADKA FROM OBLAST_TISK WHERE OBLAST_ID = 0;

----------------------------------------------------------------------------
--- PO SKO�EN�, P�EHLEDY
----------------------------------------------------------------------------

-- chybn� ozna�en� miny
SELECT X, Y FROM CHYBNE_MINY WHERE OBLAST_ID = 0;

-- p�ehledy
SELECT * FROM HRA;
SELECT * FROM VITEZOVE;
SELECT * FROM PORAZENI;

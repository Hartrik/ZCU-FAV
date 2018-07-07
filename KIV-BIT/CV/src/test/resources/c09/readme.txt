Cvičení č.8 - Procvičení hashovacích funkcí

Cíle: - vyzkoušení základních hash funkcí pro ověření integrity dat
      - použití základních hash funkcí pro generování náhodných čísel
      - (implementace) vlastní jednoduché hash funkce

1. Vyzkoušení základních hash funkcí pro ověření integrity dat
-a) Vytvořte md5 hash zprávy zprava.txt
Jaká je délka otisku?

-b) Máte následující sha1 hash otisk a zprávu zprava2.txt
22544abc54c14ca60316ef6c00a3f10e0fc3cb90
Je obsah této zprávy narušen nebo je zpráva z pohledu datové integrity v pořádku?
Je možné určit původní zprávu?

-c) Máte následující hash otisk a zprávu zprava3.txt
07b3c9f88cf16f76479399898cafbe81470961fdf57f336717776bb2c6987388d1a15fb4c9f2f823ae2d282466255fb4b5a07dc24ab48316ec400835b0b347b0
Je obsah této zprávy porušen nebo je zpráva z pohledu datové integrity v pořádku?
Jaká je délka otisku?

Informace: 
 - pro hash funkce použijte funkce md5sum, sha1sum a sha512sum


2. Vyzkoušejte generování náhodných čísel pomocí hash funkcí (alespon 3 způsoby)

Informace:
 - více viz materiály z přednášek

3. Informace o ukládání hesel Linux (Windows) - ukládání v podobě hash řetězců
Seznamte se z obsahem souboru
/etc/shadow

sudo more /etc/shadow - prohlédnutí
Popis
1. User name : It is your login name
2. Password: It your encrypted password. The password should be minimum 6-8 characters long including special characters/digits
  $1$  => zalozeno na md5
  $2a$ => je to Blowfish
  $6$ => sha512
3. Last password change (lastchanged): Days since Jan 1, 1970 that password was last changed
4. Minimum: The minimum number of days required between password changes i.e. the number of days left before the user is allowed to change his/her password
5. Maximum: The maximum number of days the password is valid (after that user is forced to change his/her password)
6. Warn : The number of days before password is to expire that user is warned that his/her password must be changed
7. Inactive : The number of days after password expires that account is disabled
8. Expire : days since Jan 1, 1970 that account is disabled i.e. an absolute date specifying when the login may no longer be used

Další informace:
http://www.cyberciti.biz/faq/understanding-etcshadow-file/ - popis shadow souboru
http://www.abclinuxu.cz/poradna/linux/show/205177

4. Vytvořte hash ze zadané zprávy pomocí alg. viz níže
 P = "Kdo implementuje prvni, dostava 3 body, druhy 2 body, treti 1 bod."
 hash algoritmus odvozen z alg. MD5 (viz přednáška)
- délka bloku = 32b
- 4 iterace
- inicializační vektory:
A = 54
B = 88
C = 72
D = 123
pořadí funkcí: F2, F1, F4, F3
hodnoty rotace: 3,1,5,2
K1=131, K2=12, K3=26, K4=92

Implementujte. Zašlete řešení spolu s výsledným hash otiskem zprávy P.

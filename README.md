==== Udhëzimet e hollësishme se si të ekzekutohet programi [SecureFileTransferProtocol_HybridCryptography] ====

Shkarkimi dhe instalimi i IntelliJ IDEA.
Shkarkimi dhe instalimi i Git.
Instalimi i JDK.
Shkarkoni Repozitorin permes butonit 'code' dhe me pas 'code URL'.
Klonimi i repository-it permes terminalit me komanden git clone.
Hapja e projektit, selektimi i dosjes se sapo klonuar nga GitHub.
Kontrollimi i SDK.
Importimi i projektit si maven apo gradle.
Ne fajllat Client dhe Server programi behet "run", dhe ekzekutohet, duke ofruar rezultatin e tij.


=== Nje pershkrim i shkurter dhe i sakte per fajllin "Client.java" ===

Programi Client lejon perdoruesin te ngarkoje dhe shkarkoje skedare ne menyre te sigurte nga serveri duke perdorur nje kombinim te algorimteve te kriptografise RSA dhe AES per te mbrojtur celesat dhe te dhenat. Perdoruimi i JavaFX i mundeson nje nderfaqe grafike per zgjedhjen dhe ruajten e skedareve.
Konstruktori i klienti gjeneron nje cift celesash RSA per enkriptim dhe dekriptim.
Metoda connectAndTransferFile lidh klientin me serverin; pranon celesin publik te serverit; gjeneron dhe dergon celesin AES te enkriptuar te serveri; enkripton dhe transferon skedarin e zgjedhur te serveri;
Metoda connectAndDownloadFile lidh klientin me serverin; pranon celesin publik te serverit; gjeneron dhe dergon celesin AES te enkriptuar te serveri; kerkon dhe shkarkon nje skedar nga serveri duke e dekriptuar ate lokalisht.
Metoda receiveServerPublicKey pranon dhe dekodon celesin publik te serverit.
Metoda sendEncryptedAESKey enkripton celesin AES me celesin publik te serverit dhe e dergon ate.
Metoda transferFile enkripton skedarin me celesin AES dhe e dergon ate te serveri.
Metoda requestFile kerkon nje skedar nga serveri dhe e ruan ate pasi ta kete dekriptuar.
Metoda run pyet perdoruesin nese deshiron te ngarkoje apo shkarkoje nje skedar dhe therret metodat perkatese.
Metoda uploadFile perdor nje dialog te FileChooser per te zgjedhur nje skedar per ngarkim dhe therret 'connectAndTransferFile'.
Metoda downloadFile lidh klientin me serverin, merr listen e skedareve nga serveri dhe lejon perdoruesin te zgjedhe nje skedar per shkarkim.
Metoda start therret metoden 'run' per te nisur aplikacionin JavaFX.
Metoda main e fillon aplikacionin JavaFX.
Kriptografia e perdorur eshte RSAUtils dhe AESUtils.


=== Nje pershkrim i shkurter dhe i sakte per fajllin "Server.java" ===

Ky program siguron transferimin e sigurt te skedareve duke perdorur nje kombinim te algoritmeve RSA dhe AES per te mbrojutr celesat dhe te dhenat. Serveri pranon komandat nga klienti per te listuar skedaret, per te derguar skedare dhe per te pranuar skedare te rinj.
 Metodat kryesore main gjeneron nje celes RSA per enkriptim dhe dekriptim; straton nje 'serversocket' ne portin e specifikuar dhe pret per lidhje nga klientet dhe kur nje klient lidhet, thirret 'handleclient' per te trajtuar komunikimin me klientin. 
Metoda handleclient dergon celesin publik RSA te klienti pastaj pranon celesin AES te enkriptuar nga klienti dhe e dekripton duke perdorur celesin privat RSA. Ne fund pranon dhe trajton komandat nga klienti 'list_files', 'request_file.
Metoda listFiles liston te gjithe skedaret ne dosjen e serverit dhe i dergon emrat e tyre te klienti.
Metoda receiveFile pranon nje skedar te enkriptuar nga klienti dhe e ruan ate ne dosjen e serverit. Dekripton skedarin e pranuar duke perdorur celesin AES dhe e run skedarin e dekriptuar.
Metoda sendFile dergon nje skedar te kerkuar nga klienti. FIllimisht, enkripton skedarin duke perdorur celesin AES dhe me pas e dergon te klienti.
Kriptografia e perdorur eshte RSAUtils dhe AESUtils.


=== Nje pershkrim i shkurter dhe i sakte per "RSAUtils.java" ===

Kjo klase eshte esenciale per realizimin e funksionaliteteve te sigurise ne aplikacionin dhe transferimin e skedareve duke siguruar qe te dhenat te jene te mbrojtura gjate komunikimit mes klientit dhe serverit.
'generateKeyPair' gjeneron nje cift celesash RSA publik dhe privat me nje madhesi celesi prej 2048 bitesh.
'encrypt' enkripton nje string duke perdorur celesin publik te dhene. Perdor algoritmin RSA per enkriptim dhe kthen te dhenat e enkriptuara ne forme te koduar me Base64.
'decrypt' dekripton nje string te enkriptuar duke perdorur celesin privat te dhene. Perdor algoritmin RSA per dekriptim dhe kthen te dhenat e dekriptuara si tekst te thjeshte.
'decodePublicKey' dekodon nje string te koduar me Base64 qe perfaqeson nje celes publik. Rikrijon objektin e celesit publik nga ky string duke perdorur specifikimin X509.


=== Nje pershkrim i shkurter dhe i sakte per fajllin "AESUtils.java" ===

Kjo klase eshte esenciale per realizimin e enkriptimit dhe dekriptimit te te dhenave dhe skedareve ne aplikacionin per transferimin e sigurt te skedareve duke perdorur algoritmin AES per te siguruar konfidencialitetin e te dhenave.
'generateAESKey' gjeneron nje celes AES me madhesi 256-bitesh per enkriptim dhe dekriptim.
'encryptAESKey' kodon celesin AES ne formatin Base64 per tu ruajtur ose transmetuar ne menyre te sigurt.
'decryptAESKey' dekodon nje string te koduar me Base64 dhe rikrijon celesin AES nga ky string.
'encryptFile' enkripton permbajtjen e nje skedari te dhene duke perdorur nje celes AES dhe e ruan ate ne nje skedar te daljes. Kontrollon ekzistencen e skedarit hyres dhe perdor algoritmin AES per enkriptim me nje bufer 4096-byte.
'decryptFile' dekripton permbajtjen e nje skedari te enkriptuar duke perdorur nje celes AES dhe e run ate ne nje skedar te daljes. Kontrollon ekzistencen e skedarit hyres dhe perdor algoritmin AES per dekriptim me nje celes 4096-bitesh.

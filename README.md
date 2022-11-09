# QuboLinker
Link all your qubo instances together!

This was made for SpookyGriefing but I decided to release it since im quitting griefing (got bored)

Sorry for my grammar

If you need help setting it up, join my discord server: https://discord.isnow.dev

Features:
  - Auto balancing each IP-Range
  - Auto reconnect on client when master server loses connection to internet or crashes, clients wont stop until they send all the results.
  - Sending all the output to the mastserver so no need to check output on each server
  - Auto connection thru the start socketserver module (requires being online 24/7 on each client and providing client ips at vpses.txt)
  - Auto converting CIDR notation to an ip-range
  - Status & Stop command at the master server

How does this work?
  - Server creates a SocketServer and a REST api on the Master Server
  - Clients connect to the socket server and listens for scanning requests
  - When a client recieves specific string it beggins scanning
  - Each time a client gets a hit from the qubo output it will send it to the Rest API (Sockets are skipping output for some reason, im too dumb to figure it out)
  - After finishing a scan client will auto-disconnect from the server if all output has been sent, if not it wont stop until all output has been sent.
  - Master Server will save all the output to outputs/qubolinker-(iprange).txt

How to set it up:
  - Download java 11 on every single VPS including the Master Server.
  - Download quboscanner on each single VPS and name it as qubo.jar
  - (OPTIONAL) Download client start server on every scanning VPS and create a screen to run it 24/7 (java -jar ClientStartServer.jar vpsName)
  - (OPTIONAL) Create vpses.txt at the Master Server containing all the vpses ips splitted by new lines
  - Run Server.jar on the Master Server
  - Configure the options
  - (OPTIONAL) Type "YES" after configuring timeout to enable autoConnect feature (requires vpses.txt and client start servers running)
  - If you didnt setup autoConnect and you are at Waiting for clients to connect step, launch Client.jar on each VPS that you want to scan with
  - After connecting all the VPS'es type YES and it should start to scan.

Notices:
  - Security is bad here. All security checks are strings that are hardcoded, make a pull request if you have time to fix it
  - Big IP-Ranges wont work since my IP-Range splitting code isnt perfect (make a pull request if you know how to fix it)
  - This project requires java 11 to work due to the socket api being java11+ only.
  - Any VPS provider will do (dont use famous hosts such as hetzner or ovh, they will term your VPS'es), digitalocean works the best (lasts 2 weeks with 10 VPS'es 8gb ram 4vcpu) for 5 usd
  - Do not close the master server with ctrl + c! type "stop" while scanning, if you accidentally close it you can restart the master server without providing an ip-range (clients will auto reconnect).
  - If you rescan an IP-Range it will delete the previous output so back it up first.

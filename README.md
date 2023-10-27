# Tomcat Server
# Setup
All right people. After you open this in an ide do a commit to change the ubuntu-start file to reference your own repository, then change the mariadb password in the `mariadb.settings` file and `main/src/main/webapp/META-INF/context.xml` file.
# Basic Instructions
Next, develop! If you want to make a new handler take a look at the other handlers as examples, create your handler, then add it in `main/src/main/webapp/WEB-INF/web.xml`. If you'd like to add an extra sql field to accounts, add a field to the POJO (Accounts.java), add all the getters/setters/constructors/logic to Accounts and AccountsHandler, and finally go to AccountSqlHandler and add the line in the create table script then go method by method in that file to fix anything your column add broke and create any methods you need based on the others. New Permissions or Csrf types are also really easy, just find the Enum responsible and add another field at then end (check the constructors for Permissions though).
# Run
To run: 
1. make sure you fixed and uncommented things in ubuntu-start.
2. get yourself an ubuntu server (oracle free servers work great for this I think, or maybe a high RAM raspberry pi, or a virtual machine)
3. Figure out the SSL, you'll need to replace the pem and key files. If you've got a domain name I'd use free cloudflare to make an SSL cert. If not, self sign or disable port 443 and the redirects in `server.xml`.
4. Now: copy paste ubuntu-start.sh into your Ubuntu machine and it should basically install itself. (I used Ubuntu 22.04.2 if that helps)
5. finally: inside of your cloned directory run `sudo ./pull.sh`. If that dosen't work at first use `chmod +x *.sh`. The server should be up now, be sure to setup the firewall(s) if needed then test!

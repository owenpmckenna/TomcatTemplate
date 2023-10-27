cat roadstatus.settings > ../roadstatus.settings
git restore roadstatus.settings
git pull
cat ../roadstatus.settings > roadstatus.settings
sudo docker-compose build
sudo docker-compose down
sudo docker-compose up -d

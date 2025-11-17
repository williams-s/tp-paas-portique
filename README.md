# tp-paas-portique
[📊 Backlog](Backlog/Backlog.xlsx)

## Architecture du projet
![img.png](Backlog/Architecture.png)



### Architecture du répertoire :
(ouvert à modification)

```
tp-paas-portique/
├── docker-compose.yml         
├── .env.example              
├── .env                       
├── .gitignore                
├── README.md                 
├── backend-core/
│   ├── Dockerfile
│   ├── package.json
│   ├── src/
│   └── ...
├── backend-telemetry/
│   ├── Dockerfile
│   ├── requirements.txt
│   └── src/
├── frontend/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── public/
├── config/
│   ├── mosquitto.conf
│   ├── kafka.properties
│   └── redis.conf
└── scripts/
    ├── init-db.sql
    └── seed-cache.py
```
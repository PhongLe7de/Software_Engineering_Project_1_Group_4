## Updating the Docker Hub Image
Build a new image from your `Dockerfile` and applies both a version tag and the `latest` tag.

Replace `x.y` with the new version number. `ckapiainen/otp_1_group_4-backend` backend and `ckapiainen/otp_1_group_4-frontend` frontend.

```sh
docker build -t ckapiainen/otp_1_group_4-backend:x.y -t ckapiainen/otp_1_group_4-backend:latest .
```
### Update docker hub image

```sh
docker push ckapiainen/otp_1_group_4-backend:x.y
docker push ckapiainen/otp_1_group_4-backend:latest
```

### Deploy the Update
Pull the latest image for the `app` service from Docker Hub 
```sh
docker-compose pull app
```

Rebuild and restart  app's container with the updated image:
```sh
docker-compose up -d
```


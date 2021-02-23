# worker-service

## Building
```
$chmod +x ./gradlew
$./gradlew buildDocker
```

## Pushing the docker image
```
docker push ptc/worker-service:0.0.1-SNAPSHOT
```
> The example above assumes that the registry exists and should update the version as needed 

## Additional work
* Update `Dockerfile` to include `--worker.blob.net=<REAL_END_POINT_URL>` 
```
ENTRYPOINT ["java","-jar","/app.jar", "--worker.blob.net=${ENDPOINT_URL}"]
```
* Update `build.gradle` to include `--build-arg ENDPOINT_URL=<url>`
* Additional work in regards to authentication of the jwt headers, possibly add in springboot interceptors/handlers for security

## Adding to Kubernetes yaml
The Example from https://docs.docker.com/get-started/kube-deploy/
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ptc-worker
  namespace: default
spec:
  replicas: 1
  selector:
    matchLabels:
      ptc-worker: web
  template:
    metadata:
      labels:
        ptc-worker: web
    spec:
      containers:
      - name: ptc-worker-service
        image: <IMAGE_TAG>:<VERSION>
---
apiVersion: v1
kind: Service
metadata:
  name: ptc-worker-entrypoint
  namespace: default
spec:
  type: NodePort
  selector:
    ptc-worker: web
  ports:
  - port: 8080
    targetPort: 8443
    nodePort: 3
```

`$ kubectl apply -f service.yaml`

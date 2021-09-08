# khool-sleepingbeauty
This is a sample project to look show the readiness process

## requisit

This project assume that you have docker, kubernetes, a registry
this can be resume with minikube

```bash
minikube -p sleepingbeauty start --cpus='4' --memory='16g' --disk-size=60g
eval $(minikube -p sleepingbeauty docker-env)
# create sleepingbeauty namespace and use it
kubectl create ns sleepingbeauty
kubectl config set-context --current --namespace=sleepingbeauty
```

## build and deploy the project

since we use default goals

```bash
mvn
```

it will [package k8s:resource k8s:apply](pom.xml#L79), [without test](pom.xml#L9) . therefore it should deploy 3 pods and a service.

The pod have a [livenessProbe](src/main/jkube/deployment.yml#L36) on `/live` and [readinessProbe](src/main/jkube/deployment.yml#L46) on [/ready](src/main/java/io/github/kanedafromparis/ReadinessResource.java#L14). 
It has two other endpoint :
 - [/goodnight](src/main/java/io/github/kanedafromparis/GoodNightResource.java#L14) that will toogle `ReadinessResource.sleeping` to **TRUE** and therefore break the /ready endpoint
 - [/wakeup](src/main/java/io/github/kanedafromparis/WakeUpResource.java#L14) that will toogle `ReadinessResource.sleeping` to **FALSE** and therefore restore the /ready endpoint

You can watch the pod via `watch kubectl get pods` or `kubectl get pods -w`

## let play

### look into the service
```bash
kubectl port-forward svc/sleepingbeauty 8080:80
```

you can put your pod to sleep [here](http://127.0.0.1:8080)

```bash
kubectl get pods
    
# NAME                              READY   STATUS    RESTARTS   AGE
# sleepingbeauty-f6d6dddb7-4jk8k    0/1     Running   0          1m12s
# sleepingbeauty-f6d6dddb7-q5t6b    1/1     Running   0          1m1s
# sleepingbeauty-f6d6dddb7-z49z2    1/1     Running   0          1m6s
```

with you pod forward you are still linked to your pod (sleepingbeauty-f6d6dddb7-4jk8k), but you can see this it is not ready, so neworks are not push to it any more, If you restart your `port-forward` you'll be put to another pod
but you can see that you do not have an extra pod, you have 3 pods one of which is not ready.   
I order to help diagnose you can change you pod label so you will keep it for diagnostic while spawning a new one for your running project.

```bash
kubectl label po $(kgpo -l provider=jkube | grep '0/1' | cut -d ' ' -f 1) app- group- sick=yes
```
then check you have your 3 active pods

```bash
kubectl get pods
    
# NAME                              READY   STATUS    RESTARTS   AGE
# sleepingbeauty-f6d6dddb7-4jk8k    0/1     Running   0          2m12s
# sleepingbeauty-f6d6dddb7-j8nhs    1/1     Running   0          15s
# sleepingbeauty-f6d6dddb7-q5t6b    1/1     Running   0          2m1s
# sleepingbeauty-f6d6dddb7-z49z2    1/1     Running   0          2m6s
```
 
### dive into you sick pod

Get yaml info :

```bash
kubectl get pods -l sick=yes -o name -o yaml

# apiVersion: v1
# items:
# - apiVersion: v1
#  kind: Pod
#  metadata:
#    annotations:
#      io.github.kanedafromparis/fun: "true"
#      jkube.io/git-branch: main
#      jkube.io/git-commit: 56943d3d0d87e764fc4c295f40e30cc18d646084
#      jkube.io/git-url: git@github.com:kanedafromparis/khool-sleepingbeauty.git
#    creationTimestamp: "2021-09-08T16:56:30Z"
#    generateName: sleepingbeauty-f6d6dddb7-
#    labels:
#      pod-template-hash: f6d6dddb7
#  ....
```
Check you pods log

```bash
kubectl logs -l sick=yes --tail=-1
```

Go into your pod (this practice is controversial, on the security point-of-view)

```
kubectl exec -it $(kubectl get pods -l sick=yes -o name) -- sh
```

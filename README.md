# User Service – Kubernetes Deployment with Helm

This project demonstrates deploying the `user-service` Spring Boot application using **Helm charts** on a **local Kubernetes cluster** (Rancher Desktop) and includes Redis integration.

---

## 📦 Technologies Used

- Spring Boot (User Service)
- Redis
- PostgreSQL (local/Azure)
- Docker
- Helm
- Kubernetes (via Rancher Desktop)
- Azure Application Insights (for monitoring)

---

## 🚀 Local Deployment Steps Using Helm

### ✅ Prerequisites

- Docker installed
- Kubernetes via Rancher Desktop
- Helm installed (`https://helm.sh/docs/intro/install/`)
- User service source code with Dockerfile and Helm chart

---

### 📁 Helm Chart Directory Structure

user-service-chart/
├── Chart.yaml
├── values.yaml
└── templates/
    ├── deployment.yaml
    ├── service.yaml
    ├── ingress.yaml
    ├── redis-deployment.yaml
    └── redis-service.yaml
---

### 🔧 Step-by-Step Setup

1. **Install Helm**  
   [Helm Installation Guide](https://helm.sh/docs/intro/install/)

2. **Create Helm Chart**
   ```bash
   helm create user-service-chart

### Update Hosts File (Windows)
Add the following line to your hosts file (C:\Windows\System32\drivers\etc\hosts):

```bash
127.0.0.1 userservice.localhost
```


### Verify Kubernetes is Reachable
```bash
kubectl get nodes
```

### Create Namespace
```bash
kubectl create namespace user-service
```

### Build Docker Image
```bash
docker build -t user-service:latest .
```

### Deploy via Helm
```bash 
helm install user-service ./user-service-chart --namespace user-service

# If already deployed, upgrade instead:
helm upgrade user-service ./user-service-chart -n user-service
```

### Verify Deployment
```bash 
kubectl get all -n user-service
kubectl get ingress -n user-service
kubectl get pods -n user-service
kubectl get svc -n user-service
```

### View Application Logs (Optional)
```bash
kubectl logs -n user-service deploy/user-service
```

### Restart Deployment (Optional)
```bash 
helm upgrade user-service ./user-service-chart -n user-service
kubectl rollout restart deployment/user-service -n user-service
```

## Azure Monitoring
The service integrates with Azure Application Insights to monitor application health and view logs in real time.

## 🛠️ Next Steps
    *   ✅ Integrate with Jenkins for CI/CD pipeline
    *   🚀 Deploy the application to Azure Kubernetes Service (AKS) using Helm
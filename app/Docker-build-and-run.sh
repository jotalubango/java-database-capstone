# Build the Docker image
# Run the following command in the java-database-capstone/app folder of your project:
#    Builds a Docker image named smart-clinic-backend
#    Uses the current directory (which contains the Dockerfile) as context

docker build -t smart-clinic-backend .

# Run the container
#   Once the image is built, start the container with:
#   -d runs the container in detached mode
#   -p 8080:8080 maps container port 8080 to the host
#   --name smart-clinic gives your container a name

docker run -d -p 8080:8080 --name smart-clinic smart-clinic-backend


#Step 4: Push to a Container Registry (optional)
docker tag smart-clinic-backend your-docker-username/smart-clinic-backend:latest

#Log in to Docker Hub
docker login

#Push the image
docker push your-docker-username/smart-clinic-backend:latest


#Step 5: Docker Best Practices
#Here are some best practices to follow when building Docker images and running containers:
#
#Image optimization
#Use multi-stage builds to separate build and runtime dependencies
#Start with minimal base images like Alpine or slim variants where possible
#Avoid installing unnecessary tools or dependencies in the final image
#Clean up intermediate files (e.g., Maven cache) if not using multi-stage
#Security
#Don't run containers as root unless necessary
#Use .dockerignore to avoid copying unnecessary files into the image
#Scan your images regularly for known vulnerabilities using tools like docker scan
#Container management
#Use explicit versions for base images to avoid unexpected changes
#Use health checks in production to detect container failures
#Use environment variables to configure containers at runtime
#Following these practices ensures your Dockerized apps are lean, secure, and maintainable.
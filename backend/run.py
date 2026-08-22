import os
import subprocess
import sys

def main():
    backend_dir = r"c:\Users\Ragav U\OneDrive\Desktop\Ragav Folder\Projects\Data Flow\Day-Flow-Human-Resource-Management\backend"
    env_path = os.path.join(backend_dir, ".env")
    
    # Load .env file
    if os.path.exists(env_path):
        print(f"Loading environment from {env_path}")
        with open(env_path, "r", encoding="utf-8") as f:
            for line in f:
                line = line.strip()
                if not line or line.startswith("#"):
                    continue
                if "=" in line:
                    key, val = line.split("=", 1)
                    key = key.strip()
                    val = val.strip()
                    if val.startswith('"') and val.endswith('"'):
                        val = val[1:-1]
                    elif val.startswith("'") and val.endswith("'"):
                        val = val[1:-1]
                    os.environ[key] = val
                    print(f"Loaded variable: {key}")

    # Set JAVA_HOME
    os.environ["JAVA_HOME"] = r"C:\Program Files\Java\jdk-25.0.2"
    
    # Path to mvnw
    mvnw_path = os.path.join(backend_dir, "mvnw.cmd")
    
    print("Starting Spring Boot application via mvnw.cmd...")
    try:
        subprocess.run([mvnw_path, "spring-boot:run"], cwd=backend_dir, check=True)
    except KeyboardInterrupt:
        print("Backend server stopped.")
    except Exception as e:
        print(f"Failed to start backend: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()

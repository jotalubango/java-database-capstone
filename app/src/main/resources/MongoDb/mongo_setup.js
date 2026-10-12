
--# Create user in MongoDB
use prescriptions

db.createUser({
  user: "smartc",
  pwd: "smartc123",
  roles: [
    { role: "readWrite", db: "prescriptions" }
  ]
})

--# call mongosh with authentication

mongosh "mongodb://o_seu_utilizador:a_sua_password@localhost:27017/prescriptions"

--# Authenticate or login with user
db.auth("smartc", "smartc123")

--#Check the logged-on user
db.runCommand({ connectionStatus: 1 })

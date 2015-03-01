//> mongo <db name> query3.js > resultQuery3.txt
printjson(db.practice.find( { "primary_type" : "HOMICIDE" } ).count())

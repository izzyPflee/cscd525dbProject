//> mongo <db name> query4.js > resultQuery4.txt
db.practice.ensureIndex( { "date" : 1 } )
cursor = db.practice.find( { } , { "case_number" : 1 , "_id" : 0 } ).sort( { "date" : 1 } )
while (cursor.hasNext()) {
	printjson(cursor.next());
}
db.practice.dropIndex( { "date" : 1 } )

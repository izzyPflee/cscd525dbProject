//> mongo <db name> query5.js > resultQuery5.txt
db.practice.ensureIndex( { "location" : '2d' } )
cursor = db.practice.find( { "location" : { $near : [-87.639715217, 41.878152421] } } , { "case_number" : 1 , "_id" : 0 } )
while(cursor.hasNext()){
	printjson(cursor.next());
}

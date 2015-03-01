//> mongo <db name> query2.js > resultQuery2.txt
cursor = db.practice.find( { "primary_type" : "THEFT" , "location_description" : "APARTMENT" } , { "case_number" : 1 , "_id" : 0 } )
while(cursor.hasNext()){
    printjson(cursor.next());
}

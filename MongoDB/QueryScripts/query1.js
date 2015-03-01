//> mongo <db name> query1.js > resultQuery1.txt
cursor = db.practice.find( { "day_night" : "PM" } , { "case_number" : 1, _id : 0 } );
while(cursor.hasNext()){
    printjson(cursor.next());
}

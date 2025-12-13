LOAD CSV WITH HEADERS FROM 'file:///genre.csv' AS genre
MERGE (g:Genre {id: genre.id, name: genre.name});

LOAD CSV WITH HEADERS FROM 'file:///author.csv' AS author
MERGE (a:Author {id: author.id, name: author.name});

LOAD CSV WITH HEADERS FROM 'file:///publisher.csv' AS publisher
MERGE (p:Publisher {id: publisher.id, name: publisher.name, logo: publisher.logo});

LOAD CSV WITH HEADERS FROM 'file:///collection.csv' AS collection
MERGE
  (c:Collection {id:       collection.id, name: collection.name, publicationYear: collection.publication_year,
                 complete: collection.complete, id_author: collection.id_author});

MATCH (a:Author), (c:Collection)
  WHERE a.id = c.id_author
CREATE (a)-[:WRITE]->(c);

LOAD CSV WITH HEADERS FROM 'file:///genre_collection.csv' AS genre_collection
MATCH (g:Genre), (c:Collection)
  WHERE g.id = genre_collection.id_genre AND c.id = genre_collection.id_collection
CREATE (c)-[:HAS_GENRE]->(g);

LOAD CSV WITH HEADERS FROM 'file:///edition.csv' AS edition
MATCH (p:Publisher), (c:Collection)
  WHERE p.id = edition.id_publisher AND c.id = edition.id_collection
CREATE (p)-[:EDITION {number: edition.number, price: edition.price, quantity: edition.quantity, cover: edition.cover,
                      status: edition.status}]->(c);

MATCH (a:Author)
REMOVE a.id;

MATCH (c:Collection)
REMOVE c.id;

MATCH (p:Publisher)
REMOVE p.id;

MATCH (g:Genre)
REMOVE g.id;

MATCH (c:Collection)
  WHERE c.publicationYear IS NOT NULL
SET c.publicationYear = toInteger(c.publicationYear);
MATCH ()-[e:EDITION]->()
  WHERE e.number IS NOT NULL
SET e.number = toInteger(e.number);
MATCH ()-[e:EDITION]->()
  WHERE e.quantity IS NOT NULL
SET e.quantity = toInteger(e.quantity);
MATCH ()-[e:EDITION]->()
  WHERE e.price IS NOT NULL
SET e.price = toFloat(e.price);

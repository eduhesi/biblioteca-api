FROM neo4j:latest
LABEL authors="MANOGARRAFA"

ENV NEO4J_HOME="/var/lib/neo4j" \
    NEO4J_PASSWD=anibook_qa

CMD cypher-shell --file $NEO4J_HOME/import/query.cypher -u neo4j -p anibook_qa
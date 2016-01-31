package com.kartashov.postgis.search;

import com.kartashov.postgis.antlr.QueryLexer;
import com.kartashov.postgis.antlr.QueryParser;
import com.kartashov.postgis.entities.Device;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<Device> search(String query) throws IOException {

        logger.info("Parsing search query {}", query);

        ANTLRInputStream input = new ANTLRInputStream(
                new ByteArrayInputStream(query.getBytes(StandardCharsets.UTF_8)));
        QueryLexer lexer = new QueryLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        QueryParser parser = new QueryParser(tokens);
        ParseTree tree = parser.query();

        logger.info("Expression tree: {}", tree.toStringTree(parser));

        QueryVisitor visitor = new QueryVisitor();
        String jpqlQuery = visitor.visit(tree);

        logger.info("Resulting JPQL query:\n{}", jpqlQuery);

        Query queryObject = entityManager.createQuery(jpqlQuery);
        for (Map.Entry<String, Object> entry : visitor.getParameters().entrySet()) {
            queryObject.setParameter(entry.getKey(), entry.getValue());
        }
        return queryObject.getResultList();
    }
}

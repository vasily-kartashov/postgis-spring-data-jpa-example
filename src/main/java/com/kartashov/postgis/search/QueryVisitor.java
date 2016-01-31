package com.kartashov.postgis.search;

import com.kartashov.postgis.antlr.QueryBaseVisitor;
import com.kartashov.postgis.antlr.QueryParser;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Length;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryVisitor extends QueryBaseVisitor<String> {

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    private final Map<String, Object> parameters = new HashMap<>();

    public Map<String, Object> getParameters() {
        return parameters;
    }

    private String addParameter(Object value) {
        String name = "var" + parameters.size();
        parameters.put(name, value);
        return name;
    }

    @Override
    public String visitQuery(QueryParser.QueryContext ctx) {
        return "SELECT d FROM Device AS d WHERE " + visit(ctx.expression());
    }

    @Override
    public String visitBracketExpression(QueryParser.BracketExpressionContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public String visitAndExpression(QueryParser.AndExpressionContext ctx) {
        return visit(ctx.expression(0)) + " AND " + visit(ctx.expression(1));
    }

    @Override
    public String visitPredicateExpression(QueryParser.PredicateExpressionContext ctx) {
        return visit(ctx.predicate());
    }

    @Override
    public String visitOrExpression(QueryParser.OrExpressionContext ctx) {
        return "(" + visit(ctx.expression(0)) + " OR " + visit(ctx.expression(1)) + ")";
    }

    @Override
    public String visitOperator(QueryParser.OperatorContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitIntegerValue(QueryParser.IntegerValueContext ctx) {
        return addParameter(Integer.valueOf(ctx.getText()));
    }

    @Override
    public String visitDoubleValue(QueryParser.DoubleValueContext ctx) {
        return addParameter(Double.valueOf(ctx.getText()));
    }

    @Override
    public String visitStringValue(QueryParser.StringValueContext ctx) {
        return addParameter(ctx.getText());
    }

    @Override
    public String visitAmount(QueryParser.AmountContext ctx) {
        Amount<?> amount = Amount.valueOf(ctx.getText());
        @SuppressWarnings("unchecked")
        double value = amount.doubleValue((Unit) amount.getUnit().getStandardUnit());
        return addParameter(value);
    }

    @Override
    public String visitUnit(QueryParser.UnitContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitElement(QueryParser.ElementContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitOperatorPredicate(QueryParser.OperatorPredicateContext ctx) {
        String operator = visit(ctx.operator());
        String value = visit(ctx.term());
        String reference = visitReference(ctx.reference(), parameters.get(value).getClass());
        return reference + " " + operator + " :" + value;
    }

    public String visitReference(QueryParser.ReferenceContext ctx, Class<?> type) {
        List<String> elements = ctx.element().stream().map(this::visitElement).collect(Collectors.toList());
        String base = "d." + elements.get(0);
        if (elements.size() == 1) {
            return base;
        } else {
            List<String> tail = elements.subList(1, elements.size());
            String extract = "extract(" + base + ", '" + String.join("', '", tail) + "')";
            if (type == Integer.class) {
                return "CAST(" + extract + " integer)";
            } else if (type == Double.class) {
                return "CAST(" + extract + " double)";
            } else {
                return extract;
            }
        }
    }

    @Override
    public String visitLocationPredicate(QueryParser.LocationPredicateContext ctx) {
        String reference = visit(ctx.reference());
        String location = visit(ctx.location());
        String distance = visit(ctx.amount());
        return "distance(" + reference + ", :" + location + ") <= :" + distance;
    }

    @Override
    public String visitLocation(QueryParser.LocationContext ctx) {
        double latitude = Double.valueOf(ctx.latitude().getText());
        double longitude = Double.valueOf(ctx.longitude().getText());
        Point point = geometryFactory.createPoint(new Coordinate(latitude, longitude));
        point.setSRID(4326);
        return addParameter(point);
    }

    @Override
    public String visitTerm(QueryParser.TermContext ctx) {
        if (ctx.amount() != null) {
            return visit(ctx.amount());
        } else if (ctx.value() != null) {
            return visit(ctx.value());
        } else {
            return visit(ctx.reference());
        }
    }
}

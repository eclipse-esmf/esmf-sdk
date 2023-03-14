package io.openmanufacturing.sds.aspectmodel.generator.diagram;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.Function;
import org.apache.jena.sparql.function.FunctionBase1;
import org.apache.jena.sparql.function.FunctionFactory;

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;

/**
 * {@link FunctionFactory} for the {@link getNamespace} SPARQL function.
 */
public class GetNamespaceFunctionFactory implements FunctionFactory {

   private final getNamespace getNamespace;

   /**
    */
   public GetNamespaceFunctionFactory( final Model context ) {
      this.getNamespace = new getNamespace( context.getNsPrefixURI( "" ) );
   }

   @Override
   public Function create( String s ) {
      return getNamespace;
   }

   /**
    * A custom SPARQL function which provides the namespace of a model element.
    */
   public static class getNamespace extends FunctionBase1 {

      private final String defaultNs;

      public getNamespace( String defaultNs ) {
         this.defaultNs = defaultNs;
      }

      @Override
      public NodeValue exec( NodeValue nodeValue ) {
         final Node node = nodeValue.asNode();
         if ( node.isBlank() || !node.isURI() ) {
            return NodeValue.makeNodeString( defaultNs );
         }
         final AspectModelUrn nodeUrn = AspectModelUrn.fromUrn( node.getURI() );
         return NodeValue.makeString( nodeUrn.getUrnPrefix() );
      }
   }
}

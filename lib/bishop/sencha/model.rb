module Bishop
  module Sencha
    class Model
      attr_accessor :name, :fields, :primitive_fields, :complex_fields

      def initialize( name )
        @name = name
        @fields = []
        @primitives = %W( Boolean String int Integer long Long Date )
      end
      
      def class_name
        ActiveSupport::Inflector.camelize( name )
      end

      def names
        ActiveSupport::Inflector.pluralize( name )
      end

      def primitive_fields
        fields.reject { |f| ( is_primitive( f.type ) == false ) }
      end

      def complex_fields
        fields.reject { |f| is_primitive( f.type ) }
      end

      def is_primitive( typeName )
        @primitives.include?( typeName )
      end

      def pluralize
        ActiveSupport::Inflector.pluralize( name )
      end
    end
  end
end
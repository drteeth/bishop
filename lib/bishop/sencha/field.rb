module Bishop
  module Sencha
    class Field
      attr_accessor :name, :type, :minOccurs, :nillable, :is_primitive, :java_type, :col_name, :sql_type

      def initialize( name )
        @name = name
        @minOccurs = 0
      end

      def pluralize
        ActiveSupport::Inflector.pluralize( name )
      end

      def col_name
        name.downcase
      end
      
    end
  end
end

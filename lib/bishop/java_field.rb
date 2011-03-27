module Bishop
  class JavaField
    attr_accessor :name, :type, :minOccurs, :nillable, :is_primitive, :java_type, :col_name, :sql_type

    def initialize( name )
      @name = name
      @minOccurs = 0
      @pattern_map = PatternMap.new
      @convert_map = {
        'date|long' => '%s.getTime()',
        'long|date' => 'new Date( %s )',
        'boolean|string' => '%s.toString()',
        'string|boolean' => 'Boolean.valueOf( %s )',
      }
    end

    def pluralize
      ActiveSupport::Inflector.pluralize( name )
    end
    
    def col_name
      name.downcase
    end
    
    def fk_name
      ActiveSupport::Inflector.foreign_key( ActiveSupport::Inflector.singularize( name ) )
    end
    
    def get_primitive_substitute
      # Date -> int
      # Integer -> int
      # Boolean -> int
      @pattern_map.replace(:java_primitives, type) || type
    end
    
    def convert_to_primitive( value_expression )
      #ex Date -> long  %s.getTime()
      #ex long -> Date new Date( %s )
      #ex Boolean -> String %s.toString()
      #ex String -> Boolean Boolean.valueOf( %s )
      key = "#{type.downcase}|#{get_primitive_substitute.downcase}"
      return value_expression unless @convert_map.has_key?(key)
      @convert_map[key] % value_expression
    end
    
    def upcast( value_expression )
      key = "#{get_primitive_substitute.downcase}|#{type.downcase}"
      return value_expression unless @convert_map.has_key?(key)
      @convert_map[key] % value_expression
    end

  end
end

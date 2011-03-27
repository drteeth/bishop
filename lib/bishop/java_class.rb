module Bishop
  class JavaClass
    attr_accessor :name, :fields, :primitive_fields, :complex_fields

    def initialize( name )
      @name = name
      @fields = []
      @primitives = %W( Boolean String int Integer long Long Date )
    end

    def getter
      "get#{type}()"
    end
    
    def class_name
      ActiveSupport::Inflector.camelize( name )
    end
    
    def names
      ActiveSupport::Inflector.pluralize( name )
    end

    def setter
      "set#{name}( #{type} #{} )"
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
      "#{name}s"
      
      #pluralize(1, 'person')
      # => 1 person

      #pluralize(2, 'person')
      # => 2 people

      #pluralize(3, 'person', 'users')
      # => 3 users

      #pluralize(0, 'person')
      # => 0 people
    end
  end
end

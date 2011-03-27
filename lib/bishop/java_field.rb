module Bishop
  class JavaField
    attr_accessor :name, :type, :minOccurs, :nillable, :is_primitive, :java_type, :col_name, :sql_type

    def initialize
      @minOccurs = 0
    end

    def pluralize
      "#{name}s"
    end
    
    def col_name
      name.downcase
    end

  end
end

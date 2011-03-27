module Bishop
  class Field
    attr_accessor :name, :type, :minOccurs, :nillable, :is_primitive, :java_type, :col_name, :sql_type

    def initialize(field_info={})
      # @minOccurs = 0
      @name = field_info['name'] || ''
      @type = field_info['type'] || ''
      @minOccurs = field_info['minOccurs'] || 0
      @nillable = field_info['nillable'] || false
    end

    def pluralize
      "#{name}s"
    end

  end
end

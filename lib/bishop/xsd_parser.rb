module Bishop
  class XsdParser

    def initialize
      @tns = "http://appserver.canada.com/SouthPARC/"
      @xs = "http://www.w3.org/2001/XMLSchema"
    end

    def parse(file, java_namespace)
      @doc = Nokogiri::XML(File.open(file))
      @java_ns = java_namespace
      # @xsd_dir = File.expand_path(File.dirname(file))
      # @hammer = JavaHammer.new(@xsd_dir,@out_ns)
      @hammer = JavaHammer.new(@java_ns)
      do_types
    end

    def do_types
      complexTypes = @doc.xpath("//xs:complexType", 'xs' => @xs )

      complexTypes.each do |complex_type|
        type = Type.new(complex_type, @xs)        
        @hammer.drop_on(type)
      end
    end

  end
end

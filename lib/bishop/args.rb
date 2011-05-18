module Bishop

  class Args
    def initialize
      @options = {}
    end

    def set?(key)
      !@options[key.to_sym].nil?
    end

    def value(key)
      @options[key.to_sym]
    end
    
    def to_options
      @options
    end
    
    def parse
      opts = Trollop::options do
        opt :file, ".xsd file to load", :type => :string
        opt :package, "authority (com.example)", :type => :string, :default => "com.example"
        opt :output, "output folder path", :type => :string, :default => "./generated/"
      end

      @options = opts
    end
  end
end

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
    
    def parse
      opts = Trollop::options do
        opt :file, ".sqs file to load", :type => :string
        opt :walker, "which tree walker to use", :type => :string, :default => "DefaultWalker"
      end

      @options = opts
    end
  end
end

module Bishop
  class Pattern
    attr_reader :pattern, :substitution

    # action can be :replace or :interpolate
    def initialize(pattern, substitition, action=:replace)
      @pattern = pattern
      @substitition = substitition
      @action = action
    end

    def substitute(value)
      puts "substitute: #{value} with #{@substitition}"
      if @action == :interpolate
        @substitition % value
      elsif @action == :replace
        @substitition
      else
        raise "#{@action} is not a valid substitution action."
      end
    end
  end
end

require 'nokogiri'
require 'erb'
require 'fileutils'
require 'trollop'
require 'active_support/inflector'

require 'bishop/args'
require 'bishop/xsd_parser'
require 'bishop/pattern'

require 'bishop/java/pattern_map'
require 'bishop/java/java_hammer'
require 'bishop/java/java_field'
require 'bishop/java/java_class'

require 'bishop/sencha/pattern_map'
require 'bishop/sencha/sencha_hammer'
require 'bishop/sencha/field'
require 'bishop/sencha/model'

module Bishop
  DEBUG = true
  
  class Application
    def run
      args = Args.new
      args.parse

      unless args.set?(:file)
        puts "usage: bishop -f <xsd> -p <package> -o <output folder>"
        exit(-1)
      end
      
      h = XsdParser.new
      xsd_types = h.parse(args.value(:file))
      
      generators = [Sencha::SenchaHammer.new(args.to_options), Java::JavaHammer.new(args.to_options)]

      generators.each do |g|
        g.generate( xsd_types )
      end
    end
  end
end

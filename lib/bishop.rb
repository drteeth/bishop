require 'nokogiri'
require 'erb'
require 'fileutils'
require 'trollop'

require 'bishop/args'
require 'bishop/field'
require 'bishop/type'
require 'bishop/xsd_parser'
require 'bishop/java_hammer'

module Bishop
  DEBUG = true
  
  class Application
    def run
      args = Args.new
      args.parse

      if ! args.set? :file
        puts "usage: bishop -f <xsd> -p <package>"
        exit(-1)
      end
      
      h = XsdParser.new
      xsd_types = h.parse(args.value(:file))
      
      options[:xsd_dir] = File.expand_path(File.dirname(options[:file]))
      java_generator = JavaHammer.new( options )
      java_generator.drop_on( xsd_types )
    end
  end
end

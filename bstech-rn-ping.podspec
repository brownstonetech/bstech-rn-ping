require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "bstech-rn-ping"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  bstech-rn-ping
                   DESC
  s.homepage     = "https://github.com/brownstonetech/bstech-rn-ping"
  s.license      = "MIT"
  # s.license    = { :type => "MIT", :file => "FILE_LICENSE" }
  s.authors      = { "Miles Huang" => "miles@bstech.ca" }
  s.platforms    = { :ios => "9.0" }
  s.source       = { :git => "https://github.com/brownstonetech/bstech-rn-ping.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,swift}"
  s.requires_arc = true

  s.dependency "React"
  # ...
  # s.dependency "..."
end

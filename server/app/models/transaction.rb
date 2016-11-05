class Transaction < ActiveRecord::Base
  belongs_to :payer, :class_name => 'User'
  belongs_to :payee, :class_name => 'User'
  belongs_to :request

  validates :request, uniqueness: true

  # def self.login(name, email, fbid)
  #   user = User.find_by(email: email)
  #   if !user
  #     user = User.create(name: name,
  #                        email: email,
  #                        fbid: fbid,
  #                        wallet: 0.0)
  #     puts "Created user with email: '#{email}'"
  #   else
  #     puts "Found user with email '#{email}'"
  #   end
  #   user
  # end
  
end

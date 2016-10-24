class User < ActiveRecord::Base
  validates :name, :email, :wallet, presence: true
  validates :email, uniqueness: true
  validates :wallet, :numericality => { :greater_than_or_equal_to => 0 }

  has_many :paid, :class_name => 'Transaction', :foreign_key => 'payer_id'
  has_many :earned, :class_name => 'Transaction', :foreign_key => 'payee_id'
end

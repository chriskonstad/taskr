class User < ActiveRecord::Base
  validates :name, :email, :wallet, presence: true
  validates :email, uniqueness: true
  validates :wallet, :numericality => { :greater_than_or_equal_to => 0 }
end

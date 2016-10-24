class Request < ActiveRecord::Base
  belongs_to :user
  has_one :trans, :class_name => 'Transaction', :foreign_key => 'request_id'

  validates :title, :user, :amount, :lat, :long, :due, presence: true
  validates :amount, :numericality => { :greater_than_or_equal_to => 0 }
end

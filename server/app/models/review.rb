class Review < ActiveRecord::Base
  belongs_to :reviewer, :class_name => 'User'
  belongs_to :reviewee, :class_name => 'User'
  belongs_to :request

  validates :reviewer, :reviewee, :rating, presence: true
  validates :request, uniqueness: true
  validates :rating, :inclusion => 1..5
end

class User < ActiveRecord::Base
  validates :name, :email, :wallet, :fbid, presence: true
  validates :email, uniqueness: true
  validates :fbid, uniqueness: true
  validates :wallet, :numericality => { :greater_than_or_equal_to => 0 }

  has_many :request
  has_many :actions, :class_name => 'Request', :foreign_key => 'actor_id'

  has_many :paid, :class_name => 'Transaction', :foreign_key => 'payer_id'
  has_many :earned, :class_name => 'Transaction', :foreign_key => 'payee_id'

  has_many :rated, :class_name => 'Review', :foreign_key => 'reviewer_id'
  has_many :rating, :class_name => 'Review', :foreign_key => 'reviewee_id'

  def avgRating
    reviews = Review.where(reviewee_id: id)
    avg = 0.0
    reviews.each do |r|
      avg += r.rating
    end

    avg /= reviews.length unless reviews.empty?
    return avg
  end

  def self.login(name, email, fbid)
    user = User.find_by(email: email)
    if !user
      user = User.create(name: name,
                         email: email,
                         fbid: fbid,
                         wallet: 0.0)
      puts "Created user with email: '#{email}'"
    else
      puts "Found user with email '#{email}'"
    end
    user
  end
end


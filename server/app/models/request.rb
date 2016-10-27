class Request < ActiveRecord::Base
  belongs_to :user
  has_one :trans, :class_name => 'Transaction', :foreign_key => 'request_id'

  validates :title, :user, :amount, :lat, :long, :due, presence: true
  validates :amount, :numericality => { :greater_than_or_equal_to => 0 }

  enum status: {open: 0, accepted: 1, completed: 2, canceled: 3}

  after_initialize :set_default_values

  def set_default_values
    # Default request to open
    self.status ||= Request.statuses[:open]
  end

  def Request.distance(longA, latA, longB, latB)
    deg2Rad = lambda { |d| d * (Math::PI/180.0) }
    earth_radius = 3961.0
    dlon = deg2Rad.call(longB - longA)
    dlat = deg2Rad.call(latB - latA)
    a = (Math.sin(dlat/2.0)**2.0) +
      Math.cos(deg2Rad.call(latA)) *
      Math.cos(deg2Rad.call(latB)) *
      (Math.sin(dlon/2.0)**2.0)
    c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a))
    return (earth_radius * c).abs
  end

  def Request.openNear(longitude, latitude, radius_miles)
    # Find all open, not past-due requests within radius_miles of
    # longitude/latitude
    return Request.where(status: Request.statuses[:open])
      .select { |r|
      Request.distance(longitude, latitude, r.long, r.lat) <= radius_miles &&
        Time.now <= r.due
    }
  end
end

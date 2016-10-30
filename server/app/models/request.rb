require 'finite_machine'

# Class for task requests
class Request < ActiveRecord::Base
  belongs_to :user
  belongs_to :actor, class_name: 'User'
  has_one :trans, class_name: 'Transaction', foreign_key: 'request_id'

  validates :title, :user, :amount, :lat, :longitude, :due, presence: true
  validates :amount, numericality: { greater_than_or_equal_to: 0 }

  enum status: { open: 0, accepted: 1, completed: 2, canceled: 3, paid: 4 }
  enum event: { accept: 0, reject: 1, complete: 2, cancel: 3, pay: 4 }

  after_initialize :set_default_values

  def set_default_values
    # Default request to open
    self.status ||= Request.statuses[:open]
  end

  # rubocop:disable Metrics/AbcSize
  def self.distance(long_a, lat_a, long_b, lat_b)
    deg2rad = ->(d) { d * (Math::PI / 180.0) }
    earth_radius = 3961.0
    dlon = deg2rad.call(long_b - long_a)
    dlat = deg2rad.call(lat_b - lat_a)
    a = (Math.sin(dlat / 2.0)**2.0) +
        Math.cos(deg2rad.call(lat_a)) *
        Math.cos(deg2rad.call(lat_b)) *
        (Math.sin(dlon / 2.0)**2.0)
    c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a))
    (earth_radius * c).abs
  end
  # rubocop:enable Metrics/AbcSize

  def self.open_near(longitude, latitude, radius_miles)
    # Find all open, not past-due requests within radius_miles of
    # longitude/latitude
    Request.where(status: Request.statuses[:open])
           .select do |r|
      Request.distance(longitude, latitude, r.longitude, r.lat) <= radius_miles &&
        Time.now <= r.due
    end
  end

  def self.do_edit(rid, uid, params)
    req = Request.find_by(id: rid, user_id: uid)
    req.update(params) if req
    !req.nil?
  end

  # rubocop:disable Metrics/AbcSize, Metrics/MethodLength
  # Use a FSM to handle the status of the request
  def self.handle_action(event, rid, aid)
    req = Request.find_by(id: rid)
    ret = false
    if req
      fm = FiniteMachine.new initial: req.status.to_sym
      fm.event(:accept, open: :accepted, if: -> { req.user.id != aid })
      fm.event(:reject, accepted: :open, if: -> { req.actor_id == aid })
      fm.event(:complete, accepted: :completed, if: -> { req.actor_id == aid })
      fm.event(:cancel, open: :canceled, if: -> { req.user.id == aid })
      fm.event(:pay, completed: :paid, if: -> { req.user.id == aid })

      fm.on_enter(:accepted) do
        req.update(status: :accepted,
                   actor_id: aid)
        ret = true
      end

      fm.on_enter(:open) do
        req.update(status: :open,
                   actor_id: nil)
        ret = true
      end

      fm.on_enter(:completed) do
        req.update(status: :completed)
        # TODO: Notify the original poster???
        ret = true
      end

      fm.on_enter(:canceled) do
        req.update(status: :canceled)
        ret = true
      end

      fm.on_enter(:paid) do
        req.update(status: :paid)
        # TODO: Create a Transaction to pay the actor
        ret = true
      end

      fm.handle(FiniteMachine::InvalidStateError) do
        puts "Cannot handle event '#{event}' when in state '#{fm.current}'"
      end

      fm.trigger(event.to_s.to_sym)
    end
    ret
  end
  # rubocop:enable Metrics/AbcSize, Metrics/MethodLength
end

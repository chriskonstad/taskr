class Device < ActiveRecord::Base
	belongs_to :user
	validates :registration_id, presence: true
	validates :device_type, presence: true
	validates :user_id, presence: true
	validates :registration_id, uniqueness: true
	
end

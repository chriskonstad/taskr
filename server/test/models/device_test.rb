require 'test_helper'

class DeviceTest < ActiveSupport::TestCase
  
  test "sample device" do	
  	dev = device = Device.create(registration_id: '1', 
                       device_type: 'android',
                       user_id: 1)
  	assert dev.valid?
  end

  test "invalid device without registration id" do 
  	dev = Device.create(device_type: 'android', user_id: '1')
  	assert_not_nil dev.errors[:registration_id]
  end

  test "invalid device without device type" do 
  	dev = Device.create(registration_id: '2', user_id: '1')
  	assert_not_nil dev.errors[:device_type]
  end

  test "invalid device without user_id" do 
  	dev = Device.create(registration_id: '2', device_type: 'android')
  	assert_not_nil dev.errors[:user_id]
  end

  test "device invalid without unique registration id" do
  	dev = Device.create(registration_id: '1', device_type: 'android', user_id: '1')
  	assert dev.valid?

  end
end

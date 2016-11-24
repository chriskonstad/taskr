class DeviceController < ApplicationController
  skip_before_action :verify_authenticity_token

  def show
    d = Device.where(user_id: params[:user_id]).first
    render :json => d.as_json
  end


  def create
    dev = Device.create(device_creation_params)
    if dev.id
      render :json => { dev: dev}.to_json
    else
      render nothing: true, status: 500
    end
  end

  def notify(data, collapse_key = nil)
    fcm = FCM.new("AIzaSyAfgwTlcsudSPq5xh2BVCFcQ8I4z9j3nq8")
    @dev = Device.search(params[:user_id])
    # registration_ids = params[:device_id]
    #registration_ids=["fill in the id here"] -> every device have different unique id
    #registration_ids = Device.android.map(&:registration_id) #an array of the client registration IDs
    options = {
      data: data,
      collapse_key: collapse_key || 'my_app'
    }
    response = fcm.send(@dev.registration_id, options)

  end
  


  def device_creation_params
    params.require(:device).permit(:registration_id,
                                   :device_type,
                                   :user_id)
  end
end

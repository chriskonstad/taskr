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


  def device_creation_params
    params.require(:device).permit(:registration_id,
                                   :device_type,
                                   :user_id)
  end
end

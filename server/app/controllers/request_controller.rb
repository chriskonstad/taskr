class RequestController < ApplicationController

  skip_before_action :verify_authenticity_token

  # Show request as JSON given an ID
  def show
    r = Request.where(id: params[:id]).first
    render :json => r.as_json
  end

  # Show all nearby open requests
  def nearby
    longitude = params[:longitude].to_f
    lat = params[:lat].to_f
    radius_miles = params[:radius].to_f

    if(!params.has_key?(:longitude) ||
        !params.has_key?(:lat) ||
        !params.has_key?(:radius))
      render nothing: true, status: 400
    else
      requests = Request.open_near(longitude, lat, radius_miles)
      render :json => requests.as_json
    end
  end

  # Create a request with JSON parameters
  def create
    req = Request.create(creation_params)
    if req.id
      render :json => { "id" => req.id }.to_json
    else
      render nothing: true, status: 500
    end
  end

  # Let the user edit a request, EXCLUDING some values (id, status)
  def edit
    id = params[:id]
    user_id = params[:auth][:user_id]

    if Request.do_edit(id, user_id, edit_params)
      render nothing: true
    else
      render nothing: true, status: 400
    end
  end

  # Find all requests associated
  def findByUid
    user_id = params[:user_id]
    role = params[:role]
    requests = Request.find_by_uid(user_id, role)
    render :json => requests.as_json
  end

  # Let user accept a request
  def accept
    handle_action('accept')
    notify("accept",nil)
  end

  # Let user reject a request
  def reject
    handle_action('reject')
  end

  # Let user complete a request
  def complete
    handle_action('complete')
    notify("complete",nil)
  end

  # Let user pay a requests
  def pay
    handle_action('pay')
  end

  # Let user cancel a request
  def cancel
    handle_action('cancel')
  end

  private

  def handle_action(event)
    id = params[:params][:id]
    user_id = params[:auth][:user_id]

    if Request.handle_action(event, id, user_id)
      render nothing: true
    else
      render nothing: true, status: 400
    end
  end

  def notify(status, collapse_key = nil)
    fcm = FCM.new("AIzaSyAfgwTlcsudSPq5xh2BVCFcQ8I4z9j3nq8")
    user_id = (Request.find_by(id: params[:params][:id])).user_id
    @dev = Device.find_by(user_id: user_id)

    data = {
      status: status,
      request_id: params[:params][:id]
    }

    options = {
      data: data,
      collapse_key: collapse_key || 'my_app'
    }
    response = fcm.send([@dev.registration_id], options)

  end


  def creation_params
    params.require(:request).permit(:title,
                                    :user_id,
                                    :amount,
                                    :lat,
                                    :longitude,
                                    :due,
                                    :description)
  end

  def edit_params
    params.require(:auth)
    params.require(:request).permit(
      :title,
      :amount,
      :lat,
      :longitude,
      :due,
      :description
    )
  end
end


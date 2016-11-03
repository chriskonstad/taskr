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

  # Let user accept a request
  def accept
    handle_action('accept')
  end

  # Let user reject a request
  def reject
    handle_action('reject')
  end

  # Let user complete a request
  def complete
    handle_action('complete')
  end

  # Let user pay a request
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
    puts(user_id)
    if Request.handle_action(event, id, user_id)
      render nothing: true
    else
      render nothing: true, status: 400
    end
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
    params.require(:request).permit(:title,
                                    :amount,
                                    :lat,
                                    :longitude,
                                    :due,
                                    :description)
  end
end


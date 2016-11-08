Rails.configuration.stripe = {
  :publishable_key => 'pk_test_p2G8kLlBqUQFRT7XH5YC7BWP',
  :secret_key      => 'sk_test_HWnsmI0iuGrMndEcCGbP7xtc'
}

Stripe.api_key = Rails.configuration.stripe[:secret_key]
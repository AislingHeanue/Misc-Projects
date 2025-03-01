use std::collections::HashMap;
use std::str::FromStr;

use grammar::{Branch, Grammar, ReturnType, Rule, RulePointer};
use nannou::image::{DynamicImage, Rgb, RgbImage};
use nannou::prelude::*;
use node::colour::NodeColour;
use node::{Evaluate, Node, NodeConstructor};
use ui::image::HashMap;

mod grammar;
mod node;

fn main() {
    nannou::app(model).update(update).run();
}

struct Model {
    image: RgbImage,
}

fn model(app: &App) -> Model {
    app.new_window().size(800, 800).view(view).build().unwrap();

    let width = 800;
    let height = 800;
    let mut image = RgbImage::new(width, height);

    let _a = Node::colour(Node::x(), Node::x(), Node::x());
    let _b = Node::conditional_colour(
        Node::greater_than(Node::x() * Node::y(), Node::number(0.0)),
        Node::colour(Node::x(), Node::y(), Node::number(1.0)),
        Node::colour(
            Node::y() % Node::x(),
            Node::y() % Node::x(),
            Node::y() % Node::x(),
        ),
    );

    let mut branches = HashMap::new();
    branches.insert(
        "E".to_string(),
        Branch::Colour(vec![
            Rule {
                resolve: Node::colour(Node::number(1.0), Node::number(1.0), Node::number(1.0)),
                chances: 1,
            },
            Rule {
                resolve: Node::colour(Node::number(0.0), Node::number(0.0), Node::number(0.0)),
                chances: 1,
            },
        ]),
    );

    let my_grammar = Grammar {
        branches,
        root: "E".to_string(),
    };

    let _c = my_grammar.generate(2);
    println!("a: {}", _a);
    println!("b: {}", _b);

    let generator = _b;

    for x in 0..width {
        for y in 0..height {
            let node_x = 2.0 * (x as f32 / width as f32) - 1.0;
            let node_y = 2.0 * (y as f32 / width as f32) - 1.0;
            let (r, g, b) = generator.eval(&node_x, &node_y).unwrap();
            image.put_pixel(x, y, Rgb([r, g, b]));
        }
    }

    Model { image }
}

fn update(_app: &App, _model: &mut Model, _update: Update) {}

fn view(app: &App, model: &Model, frame: Frame) {
    let draw = app.draw();
    draw.background().color(WHITE);

    // Convert the image to a texture and draw it
    let texture = wgpu::Texture::from_image(app, &DynamicImage::ImageRgb8(model.image.clone()));
    draw.texture(&texture).wh(vec2(800.0, 800.0)); // Scale to fit the window

    draw.to_frame(app, &frame).unwrap();
}

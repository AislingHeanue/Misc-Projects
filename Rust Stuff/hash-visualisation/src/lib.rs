use std::{
    fmt::{Display, Formatter},
    result::Result,
};

pub struct NodeColour {
    pub r: Box<Node>,
    pub g: Box<Node>,
    pub b: Box<Node>,
}

pub enum Node {
    Number(f32),
    Add(Box<Node>, Box<Node>),
    Mult(Box<Node>, Box<Node>),
    X,
    Y,
    //If(Box<Boolean>,Box<Numeric>,Box<Numeric>),
    RandomNumber,
}

pub enum Boolean {
    GreaterThan(Box<Node>, Box<Node>),
}

impl Display for NodeColour {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        write!(f, "Colour({}, {}, {})", self.r, self.g, self.b)
    }
}

impl Display for Node {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        match self {
            Node::Number(num) => write!(f, "{}", num),
            Node::Add(first, second) => write!(f, "Add({}, {})", first, second),
            Node::Mult(first, second) => write!(f, "Mult({}, {})", first, second),
            Node::X => write!(f, "x"),
            Node::Y => write!(f, "y"),
            Node::RandomNumber => panic!("Random Number should not be printed"),
        }
    }
}

impl NodeColour {
    pub fn eval(&self, x: &f32, y: &f32) -> Result<(u8, u8, u8), &str> {
        let r = to_u8(self.r.eval(x, y)?)?;
        let g = to_u8(self.g.eval(x, y)?)?;
        let b = to_u8(self.b.eval(x, y)?)?;
        Ok((r, g, b))
    }
}

impl Node {
    pub fn eval(&self, x: &f32, y: &f32) -> Result<f32, &str> {
        match self {
            Node::Number(num) => Ok(*num),
            Node::Add(first, second) => {
                Ok(((first.eval(x, y)? + second.eval(x, y)? + 1.0) % 2.0) - 1.0)
            }
            Node::Mult(first, second) => {
                Ok(((first.eval(x, y)? * second.eval(x, y)? + 1.0) % 2.0) - 1.0)
            }
            Node::X => Ok(*x),
            Node::Y => Ok(*y),
            Node::RandomNumber => Err("Random number must not be evaluated"),
        }
    }
}

fn to_u8(i: f32) -> Result<u8, &'static str> {
    if i < -1.0 || i > 1.0 {
        return Err("x must be between -1 and 1");
    }
    Ok(((i + 1.0) * 128.0) as u8)
}
